from fastapi import FastAPI, Request, HTTPException, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse, PlainTextResponse
from app.core.LoggingConfig import SetupLogging
from app.core.Config import Settings
from starlette.middleware.cors import CORSMiddleware
from app.api.Router import ApiRouter
from loguru import logger
from starlette.exceptions import HTTPException as StarletteHTTPException

SetupLogging()

app = FastAPI()

# Set all CORS enabled origins

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(ApiRouter, prefix="/api")


# format all errors
@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exc: HTTPException):
    logger.error(f"Error {exc}")
    return JSONResponse(
        status_code=exc.status_code,
        content={
            # "success": False,
            "message": exc.detail,
        },
    )


# Handle Starlette 401/403/404 from security dependencies
@app.exception_handler(StarletteHTTPException)
async def starlette_http_exception_handler(
    request: Request, exc: StarletteHTTPException
):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail},
    )


# Format error 422  (validate input error)
@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    formatted_errors = []
    
    for err in exc.errors():
        # 1. Lấy loại mã lỗi gốc của Pydantic (ví dụ: 'missing', 'string_too_short')
        err_type = err.get("type")
        
        # 2. ÁNH XẠ MÃ LỖI: Cứ thiếu trường hoặc trống chuỗi thì map thành 'isRequired'
        if err_type in ["missing", "string_too_short"]:
            error_message = "isRequired"
        else:
            error_message = err_type  # Các lỗi khác (sai kiểu dữ liệu...) giữ nguyên mã gốc

        formatted_errors.append({
            # Bỏ chữ "body" ở phần tử đầu tiên để lấy đúng tên trường (text, targetLang...)
            "field": ".".join(str(x) for x in err["loc"][1:]),
            "message": error_message  # Xuất ra đúng chữ 'isRequired' sạch sẽ
        })

    logger.error(f"Error 422: {str(formatted_errors)}")
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content={
            "message": "Validation error",
            "errors": formatted_errors,
        },
    )
