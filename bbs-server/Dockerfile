FROM amazoncorretto:17-alpine

ENV APP_BASE_PATH /opt/app
ENV TZ="Asia/Tokyo"

RUN addgroup -S app \
 && adduser -S app -G app \
 && mkdir -p ${APP_BASE_PATH} \
 && chown -R app:app ${APP_BASE_PATH}

USER app
