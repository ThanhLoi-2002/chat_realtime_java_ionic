import json
from fastapi.routing import APIRoute
from fastapi.responses import JSONResponse, StreamingResponse, FileResponse


class ResponseInterceptorRoute(APIRoute):
    def get_route_handler(self):
        original = super().get_route_handler()

        async def custom_handler(request):
            response = await original(request)

            # 1. Bỏ qua nếu là StreamingResponse, FileResponse hoặc không có body
            if isinstance(response, (StreamingResponse, FileResponse)) or not hasattr(response, "body"):
                return response

            # 2. Kiểm tra Content-Type: Nếu là file ảnh (image/png, image/jpeg...) -> Trả về luôn, KHÔNG wrap
            content_type = response.headers.get("content-type", "")
            if "image/" in content_type:
                return response

            # ----- Parse response body (Chỉ xử lý với JSON hoặc text thông thường)
            raw = response.body
            try:
                data = json.loads(raw)
            except Exception:
                try:
                    data = raw.decode("utf-8")
                except UnicodeDecodeError:
                    # Phòng trường hợp là file nhị phân khác lọt lưới -> Trả thẳng gốc, không sờ vào nữa
                    return response

            # ----- Get message from decorator
            message = getattr(self.endpoint, "ResponseMessage", "")

            # ----- Wrap response
            return JSONResponse(
                self.normalize_response(data, message), status_code=response.status_code
            )

        return custom_handler

    def normalize_response(self, data, message: str):
        # Nếu microservice đã trả về đúng cấu trúc → không wrap lại
        if (
            isinstance(data, dict)
            and "message" in data
            and "result" in data
        ):
            return data  # Giữ nguyên

        # Ngược lại → wrap
        return {
            "message": message,
            "result": data,
        }
