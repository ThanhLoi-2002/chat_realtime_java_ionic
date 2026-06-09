import json
from fastapi.routing import APIRoute
from fastapi.responses import JSONResponse


class ResponseInterceptorRoute(APIRoute):
    def get_route_handler(self):
        original = super().get_route_handler()

        async def custom_handler(request):
            response = await original(request)

            # ----- Parse response body
            raw = response.body
            try:
                data = json.loads(raw)
            except:
                data = raw.decode()

            # ----- Get message from decorator (NestJS Reflector equivalent)
            message = getattr(self.endpoint, "ResponseMessage", "")

            # ----- Wrap response (same as NestJS Interceptor)
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
