import { Storage } from '@ionic/storage';

const store = new Storage();
let storageCreated = false;

const initStorage = async (): Promise<void> => {
  if (!storageCreated) {
    await store.create();
    storageCreated = true;
  }
};

export const storage = {
  // Dùng Generic <T> để đảm bảo kiểu dữ liệu khi lưu
  async set(key: string, value: any): Promise<void> {
    await initStorage();
    const plainData = JSON.parse(JSON.stringify(value));
    await store.set(key, plainData);
  },

  // Trả về kiểu T hoặc null
  async get<T>(key: string): Promise<T | null> {
    await initStorage();
    return await store.get(key);
  },

  async remove(key: string): Promise<void> {
    await initStorage();
    await store.remove(key);
  },

  async getStorageSize(key: string) {
    const data = await storage.get(key);
    if (!data) return "0 KB";

    // Chuyển đối tượng thành chuỗi và tính byte
    const stringData = JSON.stringify(data);
    const sizeInBytes = new Blob([stringData]).size;

    const sizeInKB = (sizeInBytes / 1024).toFixed(2);
    const sizeInMB = (sizeInBytes / (1024 * 1024)).toFixed(2);

    return sizeInBytes > 1024 * 1024 ? `${sizeInMB} MB` : `${sizeInKB} KB`;
  },

  async checkTotalQuota() {
    console.log(navigator)
    if (navigator.storage && navigator.storage.estimate) {
      const estimate = await navigator.storage.estimate();

      // estimate.usage: Dung lượng đã dùng (bytes)
      // estimate.quota: Tổng dung lượng tối đa được phép dùng (bytes)

      const usedMB = (estimate.usage! / (1024 * 1024)).toFixed(2);
      const quotaMB = (estimate.quota! / (1024 * 1024)).toFixed(2);
      const percent = ((estimate.usage! / estimate.quota!) * 100).toFixed(2);

      console.log(`Đã dùng: ${usedMB} MB / Tổng: ${quotaMB} MB (${percent}%)`);
    } else {
      console.warn("Storage Manager API không hỗ trợ trên trình duyệt này.");
    }
  },

  /**
   * Liệt kê và tính toán dung lượng của TẤT CẢ các key trong app
   */
  async calculateAllKeysSize(): Promise<void> {
    await initStorage();
    const keys = await store.keys();
    let totalBytes = 0;

    console.log("--- Báo cáo dung lượng chi tiết ---");

    for (const key of keys) {
      const data = await store.get(key);
      const stringData = JSON.stringify(data);
      const sizeInBytes = new Blob([stringData]).size;

      console.log(`Key: [${key}] | Size: ${formatSize(sizeInBytes)}`);
      totalBytes += sizeInBytes;
    }

    console.log("-----------------------------------");
    console.log(`===> Tổng dung lượng dữ liệu: ${formatSize(totalBytes)}`);
  }
};

/**
 * Hàm tiện ích để định dạng byte sang đơn vị đọc được (KB, MB)
 */
const formatSize = (bytes: number): string => {
  if (bytes === 0) return "0 KB";
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  // Chỉ dùng 2 chữ số thập phân cho KB trở lên
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};