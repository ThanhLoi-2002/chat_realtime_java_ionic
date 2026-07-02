
import random

import cv2
from fastapi import APIRouter, HTTPException, Response
import numpy as np
from openai import OpenAI
import requests
import urllib
from app.common.middleware.ResponseWrapper import ResponseInterceptorRoute
from app.dto.Sticker import PromptRequest
from app.service.StickerService import StickerService

from app.core.Config import settings

router = APIRouter(route_class=ResponseInterceptorRoute)

client = OpenAI(api_key=settings.OPENAI_API_KEY)
    
@router.post("/generate-spritesheet")
async def generate_spritesheet(request: PromptRequest):
    return await StickerService.generate_spritesheet(request)


# POLLINATIONS_API_KEY = "sk_zkPAx0ctPpOrxI2xuIut6w8Zajdyazmq"


# @router.post("/generate-sticker/")
# async def generate_sticker(
#     prompt: str = Form(..., description="Mô tả sticker"),
#     num_frames: int = Form(12, description="Số lượng frame"),
# ):
#     try:
#         frames = []
#         frame_size = (130, 130)
#         base_seed = random.randint(1000, 9999)

#         optimized_prompt = f"single cute {prompt}, sticker style, white background, 2d vector, centered"
#         headers = {"Authorization": f"Bearer {POLLINATIONS_API_KEY}"}

#         # Thiết lập timeout=None để tắt hoàn toàn giới hạn thời gian chờ đọc dữ liệu
#         async with httpx.AsyncClient(timeout=httpx.Timeout(None, connect=15.0)) as client:
#             for i in range(num_frames):
#                 if i > 0:
#                     await asyncio.sleep(0.5) # Giãn cách 0.5s để API dễ thở hơn

#                 current_seed = base_seed + i
#                 url = f"https://image.pollinations.ai/p/{optimized_prompt}"
#                 params = {
#                     "width": 512,
#                     "height": 512,
#                     "seed": current_seed,
#                     "nologo": "true",
#                     # "t": int(time.time() * 1000) + i
#                 }

#                 # --- CƠ CHẾ RETRY (THỬ LẠI KHI LỖI) ---
#                 max_retries = 3
#                 success = False

#                 for attempt in range(max_retries):
#                     print(f"--> Khởi tạo frame {i+1}/{num_frames} (Seed: {current_seed}) - Lần thử {attempt + 1}")
#                     try:
#                         response = await client.get(url, params=params, headers=headers)

#                         if response.status_code == 200:
#                             img = Image.open(io.BytesIO(response.content)).convert("RGBA")
#                             frames.append(img.resize(frame_size, Image.Resampling.LANCZOS))
#                             success = True
#                             break  # Thành công thì thoát khỏi vòng lặp retry
#                         else:
#                             print(f"⚠️ Frame {i+1} trả về mã lỗi HTTP {response.status_code}, đang thử lại...")

#                     except (httpx.ReadTimeout, httpx.RequestError) as req_err:
#                         print(f"⚠️ Thử lần {attempt + 1} thất bại do nghẽn mạng ({type(req_err).__name__})")

#                     # Nghỉ 1 giây trước khi thử lại lần tiếp theo
#                     await asyncio.sleep(1.0)

#                 # Nếu sau 3 lần thử vẫn thất bại hoàn toàn thì dừng tiến trình và báo lỗi
#                 if not success:
#                     raise HTTPException(
#                         status_code=500,
#                         detail=f"Không thể tải frame {i+1} sau {max_retries} lần thử lại do AI Server quá tải."
#                     )

#         # Kiểm tra tổng số lượng frame thu được
#         # if len(frames) < num_frames:
#         #     raise HTTPException(
#         #         status_code=500,
#         #         detail=f"Không tạo đủ dải ảnh chuyển động. Chỉ lấy được {len(frames)}/{num_frames} frames."
#         #     )

#         # Ghép thành dải ngang (Sprite Sheet)
#         sprite_sheet = Image.new("RGBA", (130 * len(frames), 130), (0, 0, 0, 0))
#         for index, frame in enumerate(frames):
#             sprite_sheet.paste(frame, (index * 130, 0), frame)

#         output_buffer = io.BytesIO()
#         sprite_sheet.save(output_buffer, format="PNG")
#         output_buffer.seek(0)

#         return StreamingResponse(output_buffer, media_type="image/png")

#     except HTTPException as http_exc:
#         print(f"❌ THẤT BẠI: {http_exc.detail}")
#         raise http_exc
#     except Exception as e:
#         raise HTTPException(status_code=500, detail=f"Lỗi hệ thống phát sinh: {type(e).__name__} - {str(e)}")
