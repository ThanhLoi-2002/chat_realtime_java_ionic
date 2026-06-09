from pydantic import BaseModel, Field

class TranslateRequest(BaseModel):
    # strip_whitespace=True: Tự động xóa khoảng trắng ở đầu và cuối chuỗi
    # min_length=1: Bắt buộc độ dài chuỗi sau khi trim phải từ 1 ký tự trở lên
    text: str = Field(
        ...,
        min_length=1,
        strip_whitespace=True,
    )
    sourceLang: str = "auto"  # Mặc định tự phát hiện ngôn ngữ gốc
    targetLang: str = Field(
        ...,
        min_length=1,
        strip_whitespace=True,
    )  # Ngôn ngữ đích (vd: 'vi', 'en', 'ja'...)


# Định nghĩa cấu trúc dữ liệu trả về cho Spring Boot
class TranslateResponse(BaseModel):
    originalText: str
    translatedText: str


class DetectLanguageRequest(BaseModel):
    text: str = Field(
        ...,
        min_length=1,
        strip_whitespace=True,
    )
