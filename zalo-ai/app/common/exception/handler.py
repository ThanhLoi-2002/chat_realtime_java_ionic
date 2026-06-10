import logging
from fastapi import FastAPI, Request, status
from fastapi.exceptions import RequestValidationError, HTTPException
from fastapi.responses import JSONResponse
from starlette.exceptions import HTTPException as StarletteHTTPException

# Cấu hình logger (nếu file cũ của bạn import từ chỗ khác, hãy thay đổi cho đúng)
logger = logging.getLogger("uvicorn.error")

def register_exception_handlers(app: FastAPI):
    """
    Hàm đăng ký toàn bộ các Exception Handlers vào ứng dụng FastAPI
    """

    # 1. Format all errors
    @app.exception_handler(HTTPException)
    async def http_exception_handler(request: Request, exc: HTTPException):
        logger.error(f"Error {exc}")
        return JSONResponse(
            status_code=exc.status_code,
            content={
                "message": exc.detail,
            },
        )

    # 2. Handle Starlette 401/403/404 from security dependencies
    @app.exception_handler(StarletteHTTPException)
    async def starlette_http_exception_handler(
        request: Request, exc: StarletteHTTPException
    ):
        return JSONResponse(
            status_code=exc.status_code,
            content={"message": exc.detail},
        )

    # 3. Format error 422 (validate input error)
    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(request: Request, exc: RequestValidationError):
        formatted_errors = []
        
        for err in exc.errors():
            err_type = err.get("type")
            
            if err_type in ["missing", "string_too_short"]:
                error_message = "isRequired"
            else:
                error_message = err_type

            formatted_errors.append({
                "field": ".".join(str(x) for x in err["loc"][1:]),
                "message": error_message
            })

        logger.error(f"Error 422: {str(formatted_errors)}")
        return JSONResponse(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            content={
                "message": "Validation error",
                "errors": formatted_errors,
            },
        )