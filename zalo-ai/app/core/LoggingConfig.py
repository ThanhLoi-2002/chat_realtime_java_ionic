from loguru import logger
import sys

def SetupLogging():
    logger.remove()
    logger.add(sys.stderr, level="INFO", backtrace=True, diagnose=False)