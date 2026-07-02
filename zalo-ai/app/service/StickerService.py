import random
import cv2
from fastapi import HTTPException, Response
import numpy as np
import requests
import urllib

from app.dto.Sticker import PromptRequest

class StickerService:

    @staticmethod
    async def generate_spritesheet(req: PromptRequest):
        if not req.prompt:
            raise HTTPException(status_code=400, detail="Prompt không được để trống")
    
        system_optimized_prompt = (
            f"A 2D sprite sheet, flat design, 12 frames. "
            f"Subject: {req.prompt}. "
            f"A seamless frame-by-frame walking animation loop consisting of smooth sequential keyframes. "
            f"Isolated characters on a clean, pure solid white background, high contrast, sharp details."
        )
        
        try:
            encoded_prompt = urllib.parse.quote(system_optimized_prompt)
            random_seed = random.randint(1, 999999)
            flux_url = f"https://image.pollinations.ai/p/{encoded_prompt}?width=800&height=800&model=flux&nologo=false&seed={random_seed}"
            
            response = requests.get(flux_url)
            if response.status_code != 200:
                raise HTTPException(status_code=502, detail="Hệ thống AI đang bận.")
                
            nparr = np.frombuffer(response.content, np.uint8)
            img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
            
            gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            _, thresh = cv2.threshold(gray, 240, 255, cv2.THRESH_BINARY_INV)
            contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
            
            frames_data = []
            for cnt in contours:
                x, y, w, h = cv2.boundingRect(cnt)
                if w > 20 and h > 20:
                    frames_data.append(((x, y), img[y:y+h, x:x+w]))
            
            if not frames_data:
                raise HTTPException(status_code=500, detail="Không tìm thấy frame nào.")
                
            frames_data = sorted(frames_data, key=lambda item: (item[0][1], item[0][0]))
            frames = [item[1] for item in frames_data]

            # CHỈNH KÍCH THƯỚC FRAME LÀ 130x130
            target_size = 130
            standardized_frames = []
            for f in frames:
                # Resize
                h, w = f.shape[:2]
                scale = min(target_size / w, target_size / h)
                nw, nh = int(w * scale), int(h * scale)
                resized = cv2.resize(f, (nw, nh), interpolation=cv2.INTER_AREA)

                # Chuyển sang BGRA
                rgba = cv2.cvtColor(resized, cv2.COLOR_BGR2BGRA)

                # Tạo mask nền trắng
                white = np.all(resized > [245, 245, 245], axis=2)

                # Alpha
                rgba[:, :, 3] = np.where(white, 0, 255).astype(np.uint8)

                # Canvas trong suốt
                canvas = np.zeros((target_size, target_size, 4), dtype=np.uint8)

                top = (target_size - nh) // 2
                left = (target_size - nw) // 2

                canvas[top:top+nh, left:left+nw] = rgba

                standardized_frames.append(canvas)
            
            final_spritesheet = np.hstack(standardized_frames)
            _, encoded_img = cv2.imencode('.png', final_spritesheet)
            
            # TRẢ VỀ KẾT QUẢ KÈM THÔNG TIN FRAME
            # Sử dụng Response với custom headers để chứa metadata
            return Response(
                content=encoded_img.tobytes(), 
                media_type="image/png",
                headers={
                    "frame-count": str(len(standardized_frames)),
                    # "width": str(final_spritesheet.shape[1])
                }
            )
            
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Lỗi: {str(e)}")