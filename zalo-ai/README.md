## Cài đặt

### 1. Tạo và kích hoạt virtual environment

```bash
# Tạo virtual environment
python -m venv venv --without-pip

# Kích hoạt (Windows)
venv\Scripts\activate
```

### 2. Cài đặt dependencies

```bash
pip install -r requirements.txt
```

## Chạy ứng dụng

### Chạy local

```bash
uvicorn app.main:app --reload
```

Ứng dụng sẽ chạy tại `http://localhost:8000`

### API Documentation

Khi ứng dụng đang chạy, truy cập:
- **Swagger UI**: `http://localhost:8000/docs`
- **ReDoc**: `http://localhost:8000/redoc`


pip freeze > requirements.txt

pip install -r requirements.txt