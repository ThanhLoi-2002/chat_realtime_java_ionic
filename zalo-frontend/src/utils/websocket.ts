import SockJS from "sockjs-client"
import { Client } from "@stomp/stompjs"
import { getKey } from "./local"
import { ACCESS_TOKEN } from "./constant"

// const socket = new SockJS(`${import.meta.env.VITE_API_URL}/api/ws`)

export const stompClient: Client = new Client({
  // Sử dụng webSocketFactory để tạo mới SockJS mỗi lần kết nối
  webSocketFactory: () => {
    return new SockJS(`${import.meta.env.VITE_API_URL}/api/ws`);
  },
  reconnectDelay: 5000
})

export const connectSocket = async () => {
  const token = getKey(ACCESS_TOKEN);
  if (!token) return;

  // Cập nhật headers mới nhất
  stompClient.connectHeaders = {
    Authorization: `Bearer ${token}`
  };

  // Nếu đang active (do login nhanh quá hoặc chưa ngắt xong)
  if (stompClient.active) {
    console.log("Socket is already active, deactivating first...");
    await stompClient.deactivate();
  }

  console.log('STOMP: Activating...');
  stompClient.activate();
};

export const disconnectSocket = async () => {
  if (stompClient) {
    console.log("STOMP: Deactivating...");
    await stompClient.deactivate();

    // Quan trọng: Xóa headers để tránh dùng token cũ cho lần sau nếu login user khác
    stompClient.connectHeaders = {};
  }
};

export const sockJSSendMessage = (data: any, destination: string) => {

  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: `/app/${destination}`,
      body: JSON.stringify(data)
    })
  } else {
    console.warn("STOMP chưa kết nối. Không thể gửi tin nhắn đến:", destination);
  }

}

export const socketSubscribe = (destination: string, callback: any) => {
  if (stompClient.connected) {
    console.log(`${destination}`)
    return stompClient.subscribe(`${destination}`, callback)
  }

  const interval = setInterval(() => {

    if (stompClient.connected) {
      clearInterval(interval)
      stompClient.subscribe(`${destination}`, callback)
    }

  }, 100)

}