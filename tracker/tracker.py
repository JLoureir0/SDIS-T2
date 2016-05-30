#! /usr/bin/env python2

import BaseHTTPServer
import json
import sqlite3
import urlparse

HOST_NAME = "192.168.0.14"

PORT_NUMBER = 54321

class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def updateUser(s,user):
        database = sqlite3.connect('tracker.sqlite')
        values = '"'+user["id"]+'","'+user["ip"]+'","'+user["port"]+'","'+user["username"]+'"'
        sqlinsert = 'INSERT INTO tracker VALUES("{}","{}","{}","{}")'.format(user["id"],user["ip"],user["port"],user["username"])
        sqlupdate = 'UPDATE tracker SET ip="{}",port="{}",username="{}" WHERE id="{}"'.format(user["ip"],user["port"],user["username"],user["id"])

        with database:
            try:
                database.execute(sqlinsert)
            except sqlite3.IntegrityError:
                database.execute(sqlupdate)
            database.commit()
        database.close()

    def getUser(s,userID):
        database = sqlite3.connect('tracker.sqlite')
        sql = 'SELECT * FROM tracker WHERE id="' + userID + '"'

        with database:
            cursor = database.cursor()
            cursor.execute(sql)
            user = cursor.fetchone()
        database.close()
        return user

    def do_HEAD(s):
        s.send_response(200)
        s.send_header("Content-type", "application/json")
        s.end_headers()
    def do_POST(s):
        s.send_response(200)
        s.send_header("Content-type", "application/json")
        content_len = int(s.headers.getheader('content-length',0))
        data_string = s.rfile.read(content_len)
        s.end_headers()

        user = json.loads(data_string)
        s.updateUser(user)
    def do_GET(s):
        s.send_response(200)
        s.send_header("Content-type", "application/json")
        s.end_headers()

        userID = urlparse.parse_qs(urlparse.urlparse(s.path).query)['id'][0]
        user = s.getUser(userID)
        userJSON = "{"+'"id":"{}","ip":"{}","port":"{}","username":"{}"'.format(user[0],user[1],user[2],user[3])+"}"
        s.wfile.write(userJSON)


if __name__ == '__main__':
    server_class = BaseHTTPServer.HTTPServer
    httpd = server_class((HOST_NAME, PORT_NUMBER), MyHandler)
    print("Server Starts - %s:%s" % (HOST_NAME, PORT_NUMBER))
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()
    print("Server Stops - %s:%s" % (HOST_NAME, PORT_NUMBER))
