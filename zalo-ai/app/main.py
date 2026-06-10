from fastapi import FastAPI
from app.common.exception.handler import register_exception_handlers
from app.core.LoggingConfig import SetupLogging
from starlette.middleware.cors import CORSMiddleware
from app.api.Router import ApiRouter

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


register_exception_handlers(app)