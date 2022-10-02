# Android-20-3-Group20
## Hướng dẫn sử dụng git
### Bước 1: Clone dự án
- Git init
- Git clone https://github.com/ndphong812/Android-20-3-Group20.git
### Bước 2:Tạo branch mới theo chức năng. 
- Git checkout -b <tên branch>, ví dụ: git checkout -b login-ui
### Bước 3: Add vào local, và push (push vào branch dev)
- git add .
- git commit -m "message"
- git push origin dev 
- Nếu nó không cho push, tức đã xảy ra xung đột file, thì git pull origin dev, xử lý conflict, rồi thực hiện push lại.
