FROM mysql:8.0.28

EXPOSE 3306
ENV MYSQL_ROOT_USER 'root'
ENV MYSQL_ROOT_PASSWORD 'password'
ENV MYSQL_USER 'app'
ENV MYSQL_PASSWORD 'password'
ENV TZ 'Asia/Tokyo'

# Set MySQL character and max allowed packet
RUN { \
    echo '[client]'; \
    echo 'default-character-set=utf8mb4'; \
    echo '[mysqld]'; \
    echo 'max_allowed_packet=32MB'; \
    echo 'character-set-server=utf8mb4'; \
    echo 'collation-server=utf8mb4_bin'; \
    echo 'default-authentication-plugin=mysql_native_password'; \
} > /etc/mysql/conf.d/charset.cnf
