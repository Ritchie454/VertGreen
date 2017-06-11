mvn package shade:shade
xcopy /s "C:\Users\Ritchie__\VertBot\VertBot\config.yaml" "C:\Users\Ritchie__\VertBot\VertBot\target\"
xcopy /s "C:\Users\Ritchie__\VertBot\VertBot\credentials.yaml" "C:\Users\Ritchie__\VertBot\VertBot\target\"
xcopy /s "C:\Users\Ritchie__\VertBot\VertBot\Run.bat" "C:\Users\Ritchie__\VertBot\VertBot\target\"
exit