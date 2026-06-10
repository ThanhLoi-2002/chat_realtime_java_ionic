import os
import fasttext
from deep_translator import GoogleTranslator
from fastapi import HTTPException
from app.dto.Translate import TranslateRequest, TranslateResponse, DetectLanguageRequest
import re

class TranslateService:
    # Biến static cấp Class - Python chỉ chạy dòng này 1 lần duy nhất khi khởi động hệ thống
    BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    MODEL_PATH = os.path.join(BASE_DIR, "model", "ai", "lid.176.bin")
    
    print("--- ĐANG LOAD MODEL FASTTEXT VÀO RAM (CLASS STATIC) ---")
    ai_model = fasttext.load_model(MODEL_PATH)
    print("--- LOAD MODEL THÀNH CÔNG! ---")

    @staticmethod
    def translateMessage(request: TranslateRequest):
        try:
            translated = GoogleTranslator(
                source=request.sourceLang, 
                target=request.targetLang
            ).translate(request.text)
            
            return TranslateResponse(
                originalText=request.text,
                translatedText=translated
            )
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Lỗi hệ thống AI: {str(e)}")

    @staticmethod
    def detectLanguage(request: DetectLanguageRequest) -> dict:
        text = request.text.strip()

        # Loại bỏ memntion trả về text thuần @name
        pattern = r'\[mention:\d+\]@([^\[]+)\[/mention\]'

         # re.sub sẽ thay thế toàn bộ cụm regex khớp bằng giá trị của Group 1 (\1)
        cleanText = re.sub(pattern, r'\1', text)
        print(cleanText)

        try:
            labels, scores = TranslateService.ai_model.predict(cleanText)
            # print({
            #     "language": labels[0].replace("__label__", ""),
            #     "confidence": round(float(scores[0]), 4)
            # })

            return {
                "language": labels[0].replace("__label__", ""),
                "confidence": round(float(scores[0]), 4)
            }
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Lỗi nhận diện ngôn ngữ: {str(e)}")