from fastapi import APIRouter
from app.api.routes import (
    Translate
)

ApiRouter = APIRouter()
ApiRouter.include_router(Translate.router, tags=["translate"], prefix="/translate")
