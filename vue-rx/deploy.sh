rm -rf ./dist && gulp build && rm -rf ~/public_html/pomodoro && cp -r dist ~/public_html/pomodoro
cp notification.mp3 ~/public_html/pomodoro
