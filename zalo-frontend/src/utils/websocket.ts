import SockJS from "sockjs-client";
import { Client, IFrame } from "@stomp/stompjs";
import { getKey } from "./local";
import { ACCESS_TOKEN } from "./constant";

export const stompClient: Client = new Client({
  webSocketFactory: () => {
    // Luôn sử dụng URL từ biến môi trường
    return new SockJS(`${import.meta.env.VITE_API_URL}/api/ws`);
  },
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,
});

// Debug log để theo dõi trạng thái
stompClient.onConnect = (frame: IFrame) => {
  console.log("STOMP: Connected", frame.headers['user-name'] || '');
};

stompClient.onStompError = (frame) => {
  console.error("STOMP: Error", frame.body);
};

export const connectSocket = async () => {
  const token = getKey(ACCESS_TOKEN);
  if (!token) {
    console.warn("STOMP: No token found, skipping connection.");
    return;
  }

  // Nếu đang trong quá trình ngắt kết nối hoặc đang active, đợi một chút
  if (stompClient.active) {
    console.log("STOMP: Socket is already active.");
    return; // Không cần deactivate rồi activate lại liên tục
  }

  // Cập nhật headers
  stompClient.connectHeaders = {
    Authorization: `Bearer ${token}`
  };

  try {
    console.log('STOMP: Activating...');
    stompClient.activate();
  } catch (err) {
    console.error("STOMP: Activation error", err);
  }
};

export const disconnectSocket = async () => {
  if (stompClient.active) {
    console.log("STOMP: Deactivating...");
    try {
      await stompClient.deactivate();
      stompClient.connectHeaders = {};
      console.log("STOMP: Deactivated successfully");
    } catch (err) {
      console.error("STOMP: Deactivation error", err);
    }
  }
};

export const sockJSSendMessage = (data: any, destination: string) => {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: `/app/${destination}`,
      body: JSON.stringify(data)
    });
  } else {
    console.warn("STOMP: Not connected. Cannot send to:", destination);
  }
};

/**
 * Cải tiến Subscribe: Tránh dùng setInterval gây tốn tài nguyên
 */
export const socketSubscribe = (destination: string, callback: (message: any) => void) => {
  // Nếu đã kết nối thì subscribe ngay
  if (stompClient.connected) {
    return stompClient.subscribe(destination, callback);
  }

  // Nếu chưa kết nối, sử dụng callback onConnect để tự động subscribe khi vừa connect xong
  const originalOnConnect = stompClient.onConnect;
  stompClient.onConnect = (frame) => {
    if (originalOnConnect) originalOnConnect(frame);
    console.log(`STOMP: Auto-subscribing to ${destination}`);
    stompClient.subscribe(destination, callback);
  };
};