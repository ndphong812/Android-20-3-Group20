# Android-20-3-Group20
## Hướng dẫn sử dụng git
### Bước 1: Clone dự án
- Git init
- Git clone https://github.com/ndphong812/Android-20-3-Group20.git
### Bước 2: Tạo branch mới theo chức năng. 
- Git checkout -b <tên branch>, ví dụ: git checkout -b login-ui
- Kiểm tra các branch: git branch
- Nhảy sang branch khác: git checkout <tên branch>
### Bước 3: Add vào local, và push (push vào branch dev)
- git add .
- git commit -m "message"
- git push origin <tên branch đang code>, không phải branch dev hay main nhé. 
- Nếu nó không cho push, tức đã xảy ra xung đột file, thì git pull origin dev, xử lý conflict, rồi thực hiện push lại.
### Bước 4: Pull request
- Sau khi xong chức năng, thì thực hiện pull request, merge vào Branch dev.
- T6 hoặc T7 hàng tuần review code, Code ok thì approve pull request vào branch dev (không tự ý approve)
### Lưu ý: trước khi code thì nên Pull từ trên branch dev về, không pull thì lúc push xảy ra xung đột thì phải pull lại (Branch dev có review code trước rồi, nên không lo pull về bị xung đột)
