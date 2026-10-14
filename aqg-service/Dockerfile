FROM python:3.14-slim-trixie

WORKDIR /app
COPY requirements.txt requirements.txt
RUN pip3 install --no-cache-dir -r requirements.txt
COPY app app

# Not root: a flaw in a document parser should not hand over the container
RUN useradd --create-home --uid 10001 aqg \
    && mkdir -p /data/materials \
    && chown -R aqg /data /app
USER aqg

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
