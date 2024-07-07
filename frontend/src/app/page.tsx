"use client";

import { ProChat } from "@ant-design/pro-chat";
import { Divider, Empty, Space } from "antd";
import { useTheme } from "antd-style";
import { useEffect, useState, useRef } from "react";
import { Card } from "antd";
import Meta from "antd/es/card/Meta";
import Image from "next/image";

export default function Home() {
  const wsSessionRef = useRef<WebSocket | null>(null);
  const [recommendations, setRecommendations] = useState<
    {
      title: string;
      artist: string;
      coverImage: string;
    }[]
  >([]);

  useEffect(() => {
    let isDevEnv = process.env.NEXT_PUBLIC_ENVIRONMENT === "local";
    console.log(process.env.NEXT_PUBLIC_ENVIRONMENT);
    let wsAddress = isDevEnv
      ? "ws://localhost:8080/chat"
      : "ws://" + location.host + "/chat";
    wsSessionRef.current = new WebSocket(wsAddress);
    wsSessionRef.current.addEventListener("open", function (event) {
      console.log("WebSocket is open now.");
    });
    return () => {
      if (wsSessionRef.current) wsSessionRef.current.close();
    };
  }, []);

  const theme = useTheme();
  return (
    <main
      style={{
        background: theme.colorBgLayout,
        height: "100vh",
        display: "flex",
        flexDirection: "row",
      }}
    >
      <ProChat
        helloMessage="Welcome to Discogs AI chat with David!"
        placeholder="Please insert a message to David!"
        request={async (messages) => {
          return new Promise<Response>((resolve) => {
            if (!wsSessionRef.current) return;
            console.log(messages[messages.length - 1].content as string);
            wsSessionRef.current.addEventListener("message", function (event) {
              setRecommendations(
                recommendations.concat(JSON.parse(event.data).recommendations)
              );
              resolve(new Response(JSON.parse(event.data).message));
            });
            wsSessionRef.current.send(
              messages[messages.length - 1].content as string
            );
          });
        }}
      />
      <Divider type="vertical" style={{ height: "100%" }} />

      <Space
        direction="vertical"
        style={{
          minWidth: "250px",
          maxWidth: "20vw",
          display: "flex",
          justifyContent: "start",
          flexDirection: "column",
          alignItems: "center",
          margin: "20px",
          overflowY: "scroll",
        }}
      >
        {recommendations.length === 0 ? (
          <Empty description="No recommendations (yet)" />
        ) : (
          recommendations.map((rec, index) => (
            <Card
              key={index}
              style={{ width: 200 }}
              bordered={true}
              cover={
                <img
                  alt="Cover image of a record"
                  src={rec.coverImage}
                  width={200}
                  height={200}
                />
              }
            >
              <Meta title={rec.title} description={rec.artist} />
            </Card>
          ))
        )}
      </Space>
    </main>
  );
}
