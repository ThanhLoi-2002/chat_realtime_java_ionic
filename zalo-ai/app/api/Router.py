from fastapi import APIRouter
from app.api.routes import (
    Translate, Sticker
)

ApiRouter = APIRouter()
ApiRouter.include_router(Translate.router, tags=["translate"], prefix="/translate")
ApiRouter.include_router(Sticker.router, tags=["sticker"], prefix="/sticker")
