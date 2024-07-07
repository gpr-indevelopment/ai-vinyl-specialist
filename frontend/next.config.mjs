/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  transpilePackages: [
    "@ant-design/pro-chat",
    "@ant-design/pro-editor",
    "react-intersection-observer",
    "shiki",
  ],
  output: "export",
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "i.discogs.com",
        port: "",
        pathname: "**",
      },
    ],
  },
};

export default nextConfig;
