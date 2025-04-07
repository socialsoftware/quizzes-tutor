FROM python:3.8-slim-buster

WORKDIR /app

COPY requirements.txt requirements.txt
#RUN apt-get update \
#     && apt-get -y install libpq-dev musl-dev python3-dev libffi-dev cargo
# RUN apt-get install -y build-essential

RUN apt-get update && \
    apt-get install -y gcc && \
    pip3 install -r requirements.txt && \
    apt-get purge -y gcc && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
COPY . .

CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0"]