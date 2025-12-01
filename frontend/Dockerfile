# -----------------------
# 1) Build Stage
# -----------------------
FROM node:18-alpine as builder

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build


# -----------------------
# 2) Run Stage (Nginx)
# -----------------------
FROM nginx:alpine

WORKDIR /usr/share/nginx/html

# build 결과물 복사
COPY --from=builder /app/build .

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
