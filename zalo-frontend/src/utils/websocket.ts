import SockJS from "sockjs-client"
import { Client } from "@stomp/stompjs"

const socket = new SockJS(`${import.meta.env.VITE_API_URL}/api/ws`)

export const stompClient: Client = new Client({
  webSocketFactory: () => socket,
  reconnectDelay: 5000
})

export const connectSocket = (token: string) => {
  if (!stompClient.active) {
    stompClient.connectHeaders = {
      Authorization: `Bearer ${token}`
    }

    stompClient.activate()
    console.log('connectSocket')
  }
}

export const disconnectSocket = () => {
  if (stompClient.active) {
    stompClient.deactivate()
    console.log('disconnectSocket')
  }
}

export const sockJSSendMessage = (data: any, destination: string) => {

  stompClient.publish({
    destination: `/app/${destination}`,
    body: JSON.stringify(data)
  })

}

export const socketSubscribe = (destination: string, callback: any) => {
  console.log(`${destination}`)
  if (stompClient.connected) {
    return stompClient.subscribe(`${destination}`, callback)
  }

  const interval = setInterval(() => {

    if (stompClient.connected) {
      clearInterval(interval)
      stompClient.subscribe(`${destination}`, callback)
    }

  }, 100)

}