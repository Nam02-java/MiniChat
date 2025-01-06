Dự án nhỏ tạo một mini chat giữa 2 user và nhiều client ( 1 user có thể có nhiều thiết bị , mỗi thiết bị là một client )

Thể hiện kĩ năng xử lý những vấn đề thường thấy trong 1 app chat như 

1. khi user A load lịch sử mà có user B gửi tin nhắn tới , để tránh chen tin mới vào chuỗi tin lịch sử đang load , tách việc load lịch sử là việc của user ,  khi client yêu cầu load lịch sử thì client sẽ gọi API , API đó được đặt ở server với phương thức lấy dữ liệu tin nhắn cũ ở database cùng repositoy -> sau đó client nhận tin nhắn cũ với response và phân tích JSON rồi trả về cho client
2. khi 2 user A và B cùng gửi tin nhắn cho nhau cùng 1 thời điểm thường sẽ xảy ra lỗi trùng lặp ID nhưng nhờ vào cơ chế auto-increment của cơ sở dữ liệu và việc sử dụng transaction management của JPA thông qua ID được tạo tự động nhờ vào annotation @GeneratedValue(strategy = GenerationType.IDENTITY) trong lớp Message ánh xạ tới bảng messages trong database mysql , kể cả 2 user cùng chung một LocalDateTime.now() thì vấn đề trùng lặp ID đã ko còn xảy ra
