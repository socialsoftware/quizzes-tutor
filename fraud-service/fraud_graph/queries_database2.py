import psycopg2
import pandas.io.sql as psql
import create_query2
import os
class Database(object):

    def __init__(self):
        
        #A: connection details
        user = os.getenv('POSTGRES_USER', 'engineer')
        password = os.getenv('POSTGRES_PASSWORD', 'password')
        host = os.getenv('POSTGRES_HOST', 'localhost')
        port = os.getenv('POSTGRES_PORT', '5432')
        database = os.getenv('POSTGRES_DB', 'tutordb')
        
        
        #B: database connection
        self.conn = psycopg2.connect(user=user,password=password,host=host,port=port,database=database)

    def __end__(self):
        if(self.conn): self.conn.close()
    
    def get_data(self, query):
        return psql.read_sql_query(query, self.conn)
    

# if __name__ == '__main__':
#     db_ilu = Database()
#     data = db_ilu.get_data(create_query.def_query(13500))
#     print(data.head())
    

def fetch_data(query):
    db_ilu = Database()
    data = db_ilu.get_data(query)
    return data