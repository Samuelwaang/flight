docker buildx build --platform linux/amd64 -t samuelwaang/mainapp:1.0 . 
docker push samuelwaang/mainapp:1.0

cd downloads
ssh -i spring-main-app.pem ec2-user@3.145.28.235
sudo docker pull samuelwaang/mainapp:1.0
sudo docker run -d -p 80:8080 samuelwaang/mainapp:1.0