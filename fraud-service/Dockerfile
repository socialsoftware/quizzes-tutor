FROM python:3.14-slim-trixie

WORKDIR /app

# Headless container: render matplotlib with Agg instead of probing for a GUI toolkit
ENV MPLBACKEND=Agg

COPY requirements.txt requirements.txt
# Every dependency ships binary wheels for this Python, so no compiler is needed
RUN pip3 install --no-cache-dir -r requirements.txt
COPY . .

CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0"]
