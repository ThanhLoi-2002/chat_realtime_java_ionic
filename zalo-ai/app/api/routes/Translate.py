from fastapi import APIRouter
from app.common.decorator.ResponseMessage import ResponseMessage
from app.common.middleware.ResponseWrapper import ResponseInterceptorRoute
from app.dto.Translate import TranslateResponse, TranslateRequest, DetectLanguageRequest
from app.service.TranslateService import TranslateService

router = APIRouter(route_class=ResponseInterceptorRoute)

@router.post("", response_model=TranslateResponse)
@ResponseMessage("Ok")
def translateText(
    request: TranslateRequest,
):
    return TranslateService.translateMessage(request)


@router.post("/detect-language")
def detectLanguage(request: DetectLanguageRequest):
    return TranslateService.detectLanguage(request)