import urllib.parse
import pandas.io.sql as psql
from sqlalchemy import create_engine
import create_query2
import os
class Database(object):

    def __init__(self):
        
        #A: connection details
        user = urllib.parse.quote_plus(os.getenv('POSTGRES_USER', 'engineer'))
        password = urllib.parse.quote_plus(os.getenv('POSTGRES_PASSWORD', 'password'))
        host = os.getenv('POSTGRES_HOST', 'localhost')
        port = os.getenv('POSTGRES_PORT', '5432')
        database = os.getenv('POSTGRES_DB', 'tutordb')
        
        
        #B: database connection (pandas only supports SQLAlchemy connectables, not raw DBAPI ones)
        self.engine = create_engine(f'postgresql+psycopg://{user}:{password}@{host}:{port}/{database}')

    def __end__(self):
        if(self.engine): self.engine.dispose()
    
    def get_data(self, query):
        return psql.read_sql_query(query, self.engine)
    

# if __name__ == '__main__':
#     db_ilu = Database()
#     data = db_ilu.get_data(create_query.def_query(13500))
#     print(data.head())
    

def fetch_data(query):
    db_ilu = Database()
    data = db_ilu.get_data(query)
    db_ilu.engine.dispose()
    return data