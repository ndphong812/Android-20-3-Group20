# Android-20-3-Group20
## Hướng dẫn sử dụng git
### Bước 1: Clone dự án
- Git init
- Git clone https://github.com/ndphong812/Android-20-3-Group20.git
- Git checkout dev
### Bước 2: Tạo branch mới theo chức năng. 
- Git checkout -b <featute / tên branch>, ví dụ: git checkout -b feature/login-ui
- Kiểm tra các branch: git branch
- Nhảy sang branch khác: git checkout <tên branch>
### Bước 3: Add vào local, và push (push vào branch dev)
- git add .
- git commit -m "message - https://ndphong.atlassian.net/jira/software/projects/A2G/boards/1"
- git push origin <tên branch đang code>, không phải branch dev hay main nhé. 
- Nếu nó không cho push, tức đã xảy ra xung đột file, thì git pull origin dev, xử lý conflict, rồi thực hiện push lại.
### Bước 4: Pull request
- Sau khi xong chức năng, thì thực hiện pull request, merge vào Branch dev.
- T6 hoặc T7 hàng tuần review code, Code ok thì approve pull request vào branch dev (không tự ý approve)
### Lưu ý: trước khi code thì nên Pull từ trên branch dev về (git pull origin dev), không pull thì lúc push xảy ra xung đột thì phải pull lại (Branch dev có review code trước rồi, nên không lo pull về bị xung đột)
# Code Rule
## Quy tắc đặt tên trong jave: đặt kiểu Camel
## Quy tắc đặt tên hằng số, string, variable: variable_name
## Cấu trúc thư mục:
### Manifests: chứa file xml chính
### Java: chứa file .java
### Res: chứa tài nguyên cần dùng
- Drawable: chứa các hình ảnh dùng trong ứng dụng
- Layout: Các file code .xml đặt trong này. 
- Mipmap: chứa icon
- Value: Định nghĩa color, chuỗi trong này
# Tham khảo:
- P2P connections with Wi-Fi Direct: https://developer.android.com/training/connect-devices-wirelessly/wifi-direct
- SQLite with Android: https://viblo.asia/p/su-dung-sqlite-database-trong-ung-dung-android-wjAM7alevmWe
- Recommend library: https://auth0.com/blog/android-development-15-libraries-you-should-be-using
- Dark / Light theme: https://developer.android.com/develop/ui/views/theming/darktheme


# Chức năng
## Login:
- Tài khoản: username (min 6 kí tự, max 20 kí tự)
- Mật khẩu: password (min 6 kí tự, max 20 kí tự)
- Nhập lại mật khẩu: reTypePassword

## Document for Chat screen UI
Link introductions: https://youtu.be/KTf4JAMfEBg
### XML
**activity_chat.xml**: sử dụng ConstraintLayout để binding data
- Trong đó chia layout thành 3 phần: chat_header, chat_footer, chat_message(rev_messages)
- Ta sẽ sử dụng recycleView (rev_messages) để render danh sách message

### JAVA
**ChatAdapter**: Hỗ trợ render messages thành giao diện
- addChatItem(): Khi 1 item chat vào danh sách để render lại giao diện

**ChatActivity**:
- renderMessages(): Hàm render Message chính
- getListMessage(): hàm này sẽ gọi API để render ra toàn bộ message đã có
- getChatContact(): Nhận user đang chat từ màn hình chính truyển qua (Sử dụng Intent)

- imageButtonSendMessage: Đây là hàm xử lý khi người dùng send message

- Dark / Light theme: https://developer.android.com/develop/ui/views/theming/darktheme