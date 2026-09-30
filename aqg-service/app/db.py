from datetime import datetime, timezone

from sqlalchemy import JSON, DateTime, Float, ForeignKey, Integer, MetaData, String, Text, create_engine, text
from sqlalchemy.engine import Engine
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, sessionmaker
from sqlalchemy.pool import StaticPool

SCHEMA = "aqg"


class Base(DeclarativeBase):
    metadata = MetaData(schema=SCHEMA)


class MaterialRow(Base):
    __tablename__ = "material"

    id: Mapped[str] = mapped_column(String(32), primary_key=True)
    course_id: Mapped[int] = mapped_column(Integer, index=True)
    filename: Mapped[str] = mapped_column(String(255))
    status: Mapped[str] = mapped_column(String(16))
    chunk_count: Mapped[int] = mapped_column(Integer, default=0)
    parser: Mapped[str | None] = mapped_column(String(32), nullable=True)
    parse_seconds: Mapped[float | None] = mapped_column(Float, nullable=True)
    error: Mapped[str | None] = mapped_column(Text, nullable=True)


class ChunkRow(Base):
    __tablename__ = "chunk"

    id: Mapped[str] = mapped_column(String(64), primary_key=True)
    material_id: Mapped[str] = mapped_column(ForeignKey(f"{SCHEMA}.material.id", ondelete="CASCADE"), index=True)
    position: Mapped[int] = mapped_column(Integer)
    text: Mapped[str] = mapped_column(Text)
    source: Mapped[str | None] = mapped_column(String(512), nullable=True)


class JobRow(Base):
    __tablename__ = "generation_job"

    id: Mapped[str] = mapped_column(String(32), primary_key=True)
    status: Mapped[str] = mapped_column(String(16))
    prompt_version: Mapped[str] = mapped_column(String(32))
    model_id: Mapped[str] = mapped_column(String(128))
    grounding_mode: Mapped[str] = mapped_column(String(16))
    outcomes: Mapped[list] = mapped_column(JSON, default=list)
    error: Mapped[str | None] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))


def normalise_url(url: str) -> str:
    """Accept the plain postgresql:// form that docker-compose and most tooling use."""
    for prefix in ("postgresql://", "postgres://"):
        if url.startswith(prefix):
            return "postgresql+psycopg://" + url[len(prefix):]
    return url


def create_session_factory(url: str) -> sessionmaker:
    url = normalise_url(url)
    if url.startswith("sqlite"):
        # SQLite has no schemas; the same models run there for tests and local development
        in_memory = url in ("sqlite://", "sqlite:///:memory:")
        engine: Engine = create_engine(
            url,
            connect_args={"check_same_thread": False},
            poolclass=StaticPool if in_memory else None,
        ).execution_options(schema_translate_map={SCHEMA: None})
        _enable_sqlite_foreign_keys(engine)
    else:
        engine = create_engine(url, pool_pre_ping=True)
        with engine.begin() as connection:
            connection.execute(text(f"CREATE SCHEMA IF NOT EXISTS {SCHEMA}"))
    Base.metadata.create_all(engine)
    return sessionmaker(engine, expire_on_commit=False)


def _enable_sqlite_foreign_keys(engine: Engine) -> None:
    from sqlalchemy import event

    @event.listens_for(engine, "connect")
    def pragma(connection, _):
        connection.execute("PRAGMA foreign_keys=ON")
