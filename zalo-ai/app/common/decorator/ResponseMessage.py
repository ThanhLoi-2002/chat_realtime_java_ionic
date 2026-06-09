
def ResponseMessage(msg: str):
    def decorator(func):
        setattr(func, "ResponseMessage", msg)
        return func
    return decorator