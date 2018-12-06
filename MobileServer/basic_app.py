import datetime
from sqlalchemy import and_, or_
from flask import Flask, request, render_template, session, Response
from flask_babelex import Babel
from flask_sqlalchemy import SQLAlchemy
from flask_user import current_user, login_required, roles_required, UserManager, UserMixin
from model import entities
from database import connector
import json
import os
from sqlalchemy.orm import sessionmaker

db = connector.Manager()
engine = db.createEngine()


# Class-based application configuration
class ConfigClass(object):
    """ Flask application config """

    # Flask settings
    SECRET_KEY = 'This is an INSECURE secret!! DO NOT use this in production!!'

    # Flask-SQLAlchemy settings
    SQLALCHEMY_DATABASE_URI = 'sqlite:///basic_app.sqlite'  # File-based SQL database
    SQLALCHEMY_TRACK_MODIFICATIONS = False  # Avoids SQLAlchemy warning

    # Flask-Mail SMTP server settings
    MAIL_SERVER = 'smtp.gmail.com'
    MAIL_PORT = 465
    MAIL_USE_SSL = True
    MAIL_USE_TLS = False
    MAIL_USERNAME = 'jonathanprieto738@gmail.com'
    MAIL_PASSWORD = 'Pokemon 1289'
    MAIL_DEFAULT_SENDER = '"MyApp" <noreply@onlinelibrarype.com>'

    # Flask-User settings
    USER_APP_NAME = "Online Library Peru"  # Shown in and email templates and page footers
    USER_ENABLE_EMAIL = True  # Enable email authentication
    USER_ENABLE_USERNAME = False  # Disable username authentication
    USER_EMAIL_SENDER_NAME = USER_APP_NAME
    USER_EMAIL_SENDER_EMAIL = "noreply@onlinelibrarype.com"


def create_app():
    """ Flask application factory """

    # Create Flask app load app.config
    app = Flask(__name__)
    app.config.from_object(__name__ + '.ConfigClass')

    # Initialize Flask-BabelEx
    babel = Babel(app)

    # Initialize Flask-SQLAlchemy
    db = SQLAlchemy(app)

    # Define the User data-model.
    # NB: Make sure to add flask_user UserMixin !!!
    class User(db.Model, UserMixin):
        __tablename__ = 'users'
        id = db.Column(db.Integer, primary_key=True)
        active = db.Column('is_active', db.Boolean(), nullable=False, server_default='1')

        # User authentication information. The collation='NOCASE' is required
        # to search case insensitively when USER_IFIND_MODE is 'nocase_collation'.
        email = db.Column(db.String(255, collation='NOCASE'), nullable=False, unique=True)
        email_confirmed_at = db.Column(db.DateTime())
        password = db.Column(db.String(255), nullable=False, server_default='')

        # User information
        first_name = db.Column(db.String(100, collation='NOCASE'), nullable=False, server_default='')
        last_name = db.Column(db.String(100, collation='NOCASE'), nullable=False, server_default='')

        # Define the relationship to Role via UserRoles
        roles = db.relationship('Role', secondary='user_roles')

    # Define the Role data-model
    class Role(db.Model):
        __tablename__ = 'roles'
        id = db.Column(db.Integer(), primary_key=True)
        name = db.Column(db.String(50), unique=True)

    # Define the UserRoles association table
    class UserRoles(db.Model):
        __tablename__ = 'user_roles'
        id = db.Column(db.Integer(), primary_key=True)
        user_id = db.Column(db.Integer(), db.ForeignKey('users.id', ondelete='CASCADE'))
        role_id = db.Column(db.Integer(), db.ForeignKey('roles.id', ondelete='CASCADE'))

    # Setup Flask-User and specify the User data-model
    user_manager = UserManager(app, db, User)

    # Create all database tables
    db.create_all()

    # Create 'member@example.com' user with no roles
    if not User.query.filter(User.email == 'member@example.com').first():
        user = User(
            email='member@example.com',
            email_confirmed_at=datetime.datetime.utcnow(),
            password=user_manager.hash_password('Password1'),
        )
        db.session.add(user)
        db.session.commit()

    # Create 'admin@example.com' user with 'Admin' and 'Agent' roles
    if not User.query.filter(User.email == 'admin@example.com').first():
        user = User(
            email='admin@example.com',
            email_confirmed_at=datetime.datetime.utcnow(),
            password=user_manager.hash_password('Password1'),
        )
        user.roles.append(Role(name='Admin'))
        user.roles.append(Role(name='Agent'))
        db.session.add(user)
        db.session.commit()

    # The Home page is accessible to anyone
    @app.route('/')
    def home_page():
        return render_template("index.html")



    # The Members page is only accessible to authenticated users
    @app.route('/biblioteca')
    @login_required  # Use of @login_required decorator
    def biblioteca():
        return render_template("biblioteca.html")

    @app.route('/biblioteca2')
    @login_required  # Use of @login_required decorator
    def biblioteca2():
        return render_template("biblioteca2.html")

    # The Admin page requires an 'Admin' role.
    @app.route('/nuevolibro')
    @roles_required('Admin')  # Use of @roles_required decorator
    def admin_new_libro():
        return render_template("nuevolibro.html")

    @app.route('/actualizarlibro')
    @roles_required('Admin')
    def admin_updatelibro():
        return render_template("updatelibro1.html")

    @app.route('/actualizarlibro2')
    @roles_required('Admin')
    def admin_updatelibro2():
        return render_template("updatelibro.html")

    @app.route('/libros', methods=['Post'])
    def create_book():
        titulo = request.form['titulo']
        autor = request.form['autor']
        genero = request.form['genero']
        nacionalidad = request.form['nacionalidad']
        descripcion = request.form['descripcion']
        imagen = request.files['imagen']
        archivo = request.files['archivo']
        fotoautor = request.files['fotoautor']
        nombreimagen = imagen.filename
        nombrearchivo = archivo.filename
        nombrefotoautor = fotoautor.filename
        rutaimagen = os.path.abspath(nombreimagen)
        rutaarchivo = os.path.abspath(nombrearchivo)
        rutafotoautor = os.path.abspath(nombrefotoautor)
        libro = entities.Libro(titulo=titulo,
                               autor=autor,
                               genero=genero,
                               nacionalidad=nacionalidad,
                               descripcion=descripcion,
                               imagen=imagen.read(),
                               archivo=archivo.read(),
                               fotoautor=fotoautor.read(),
                               nombreimagen=nombreimagen,
                               nombrearchivo=nombrearchivo,
                               nombrefotoautor=nombrefotoautor,
                               rutaimagen=rutaimagen,
                               rutaarchivo=rutaarchivo,
                               rutafotoautor=rutafotoautor)
        session = db.Session(engine)
        session.add(libro)
        session.commit()
        return render_template('success.html')

    @app.route('/libros', methods=['PUT'])
    def update_book():
        session = db.Session(engine)
        ID = request.form['key']
        libros = session.query(entities.Libro).filter(entities.Libro.ID == ID)

        content = json.loads(request.form['values'])

        for libro in libros:
            if 'titulo' in content:
                libro.titulo = content['titulo']
            if 'autor' in content:
                libro.autor = content['autor']
            if 'genero' in content:
                libro.genero = content['genero']
            if 'nacionalidad' in content:
                libro.nacionalidad = content['nacionalidad']
            if 'descripcion' in content:
                libro.descripcion = content['descripcion']
            if 'imagen' in content:
                imagen = content['imagen']
                libro.imagen = imagen
                nombreimagen = imagen.filename
                libro.nombreimagen = nombreimagen
                libro.rutaimagen = os.path.abspath(nombreimagen)
            if 'archivo' in content:
                archivo = content['archivo']
                libro.archivo = archivo
                nombrearchivo = archivo.filename
                libro.nombrearchivo = nombrearchivo
                libro.rutaarchivo = os.path.abspath(nombrearchivo)
            if 'fotoautor' in content:
                fotoautor = content['fotoautor']
                libro.fotoautor = fotoautor
                nombrefotoautor = fotoautor.filename
                libro.nombrefotoautor = nombrefotoautor
                libro.rutafotoautor = os.path.abspath(nombrefotoautor)
            session.add(libro)
        session.commit()
        return 'Update exitosa'

    @app.route('/actualizarconput')
    def update_put():
        return render_template('updatePut.html')

    @app.route('/libro/<ID>', methods=['DELETE'])
    def delete_book(ID):
        session = db.Session(engine)
        libros = session.query(entities.Libro).filter(entities.Libro.ID == ID)

        for libro in libros:
            session.delete(libro)
        session.commit()
        return 'User Deleted'

    @app.route('/titulo/<ID>', methods=['GET'])
    @login_required
    def titulo(ID):
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro).filter(entities.Libro.ID == ID)
        data = []
        for libro in libros:
            data.append(libro)
            break
        return Response(data[0].titulo, mimetype='text/txt')

    @app.route('/autor/<ID>', methods=['GET'])
    @login_required
    def autor(ID):
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro).filter(entities.Libro.ID == ID)
        data = []
        for libro in libros:
            data.append(libro)
            break
        return Response(data[0].autor, mimetype='text/txt')

    @app.route('/genero/<ID>', methods=['GET'])
    @login_required
    def genero(ID):
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro).filter(entities.Libro.ID == ID)
        data = []
        for libro in libros:
            data.append(libro)
            break
        return Response(data[0].genero, mimetype='text/txt')

    @app.route('/nacionalidad/<ID>', methods=['GET'])
    @login_required
    def nacionalidad(ID):
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro).filter(entities.Libro.ID == ID)
        data = []
        for libro in libros:
            data.append(libro)
            break
        return Response(data[0].nacionalidad, mimetype='text/txt')

    @app.route('/descripcion/<ID>', methods=['GET'])
    @login_required
    def descripcion(ID):
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro).filter(entities.Libro.ID == ID)
        data = []
        for libro in libros:
            data.append(libro)
            break
        return Response(data[0].descripcion, mimetype='text/txt')

    @app.route('/imagen/<ID>', methods=['GET'])
    @login_required
    def imagen(ID):
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro).filter(entities.Libro.ID == ID)
        data = []
        for libro in libros:
            data.append(libro)
            break
        return Response(data[0].imagen, mimetype='image/png')

    @app.route('/archivo/<ID>', methods=['GET'])
    @login_required
    def archivo(ID):
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro).filter(entities.Libro.ID == ID)
        data = []
        for libro in libros:
            data.append(libro)
            break
        return Response(data[0].archivo, mimetype='application/pdf')

    @app.route('/fotoautor/<ID>', methods=['GET'])
    @login_required
    def fotoautor(ID):
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro).filter(entities.Libro.ID == ID)
        data = []
        for libro in libros:
            data.append(libro)
            break
        return Response(data[0].fotoautor, mimetype='image/png')

    @app.route('/libros', methods=['GET'])
    @login_required
    def libros():
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro)
        data = []
        for libro in libros:
            data.append(libro)
        return Response(json.dumps(data,
                                   cls=connector.AlchemyEncoder),
                        mimetype='application/json')

    @app.route('/mobile_login', methods=["POST"])
    def mobile_login():
        body = request.get_json(silent=True)
        print(body)
        username = body['username']
        password = body['password']
        sessiondb = db.Session(engine)
        user = sessiondb.query(entities.MobileUser).filter(
            and_(entities.MobileUser.username == username, entities.MobileUser.password == password)).first()
        if user != None:
            return Response(json.dumps({'response': True, 'id': user.id},
                                       cls=connector.AlchemyEncoder
                                       ), mimetype='application/json')
        else:
            return Response(json.dumps({'response': False},
                                       cls=connector.AlchemyEncoder
                                       ), mimetype='application/json')

    @app.route('/mobile_register', methods=['POST'])
    def mobile_register():
        username = request.form['username']
        email = request.form['email']
        password = request.form['password']
        print(username, email, password)

        user = entities.MobileUser(username=username,
                             email=email,
                             password=password)
        register = db.Session(engine)
        register.add(user)
        register.commit()

        return "Todo Ok!"

    @app.route('/mobile_libros', methods=['GET'])
    def get_libros():
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro)
        data = []
        for libro in libros:
            data.append(libro)
        return Response(json.dumps({'data': data},
                                   cls=connector.AlchemyEncoder),
                        mimetype='application/json')

    @app.route('/mobile_libros/<ID>', methods=['GET'])
    def mobile_libros_id(ID):
        db_session = db.Session(engine)
        libros = db_session.query(entities.Libro).filter(entities.Libro.ID == ID)
        data = []
        for libro in libros:
            data.append(libro)
        print(data)
        return Response(json.dumps({'data': data},
                                   cls=connector.AlchemyEncoder),
                        mimetype='application/json')

    @app.route('/newUsers', methods=['GET'])
    def newUsers():
        db_session = db.Session(engine)
        users = db_session.query(entities.MobileUser)
        data = []
        for user in users:
            data.append(user)
        return Response(json.dumps({'data': data},
                                   cls=connector.AlchemyEncoder),
                        mimetype='application/json')

    return app


app = create_app()
# Start development web server
if __name__ == '__main__':
    app.run(host='localhost', port=5000, debug=True)
