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
    if (navigator.storage && navigator.storage.estimate) {
      const estimate = await navigator.storage.estimate();

      // estimate.usage: Dung lượng đã dùng (bytes)
      // estimate.quota: Tổng dung lượng tối đa được phép dùng (bytes)

      const usedMB = (estimate.usage! / (1024 * 1024)).toFixed(2);
      const quotaMB = (estimate.quota! / (1024 * 1024)).toFixed(2);
      const percent = ((estimate.usage! / estimate.quota!) * 100).toFixed(2);

      // Sử dụng
      const size = await this.getStorageSize('conversations');
      console.log(`Dung lượng danh sách chat: ${size}`);

      console.log(`Đã dùng: ${usedMB} MB / Tổng: ${quotaMB} MB (${percent}%)`);
    } else {
      console.warn("Storage Manager API không hỗ trợ trên trình duyệt này.");
    }
  },
};