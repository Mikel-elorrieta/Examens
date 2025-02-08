const express = require('express');
const mysql = require('mysql2');
const bodyParser = require('body-parser');
const cors = require('cors');


const app = express();
app.use(cors());
app.use(bodyParser.json());


// MySQL datu-baserako konexioa sortu
const db = mysql.createConnection({
   // host: '127.0.0.1', // MySQL zerbitzariaren helbidea
   
    host: 'localhost', // MySQL zerbitzariaren helbidea
    port: '3308' , // Portua 



  
   user: 'admin', // MySQL erabiltzailea
   password: '', // MySQL pasahitza
   database: 'gestion_eventos', // Datu-basearen izena
 

});


db.connect((err) => {
    if (err) {
        console.error('Errorea datu-basera konektatzean:', err);
        return;
    }
    console.log('Datu-basera konektatuta');
});


// Endpoints CRUD
app.get('/users', (req, res) => {
    const query = 'SELECT * FROM usuarios';
    db.query(query, (err, results) => {
        if (err) throw err;
        res.send(results);
    });
});

// Endpoints CRUD
app.get('/categorias', (req, res) => {
    const query = 'SELECT * FROM categorias';
    db.query(query, (err, results) => {
        if (err) throw err;
        res.send(results);
    });
});


// Endpoints CRUD
app.get('/eventos', (req, res) => {
    const query = 'SELECT * FROM eventos';
    db.query(query, (err, results) => {
        if (err) throw err;
        res.send(results);
    });
});

// Endpoints CRUD
app.get('/inscripciones', (req, res) => {
    const query = 'SELECT * FROM inscripciones';
    db.query(query, (err, results) => {
        if (err) throw err;
        res.send(results);
    });
});




app.post('/lagunak', (req, res) => {
    const newItem = req.body;
    const query = 'INSERT INTO lagunak SET ?';
    db.query(query, newItem, (err, results) => {
        if (err) throw err;
        res.send({ id: results.insertId, ...newItem });
    });
});


app.put('/lagunak/:id', (req, res) => {
    const { id } = req.params;
    const updatedItem = req.body;
    const query = 'UPDATE lagunak SET ? WHERE id = ?';
    db.query(query, [updatedItem, id], (err, results) => {
        if (err) throw err;
        res.send(results);
    });
});


app.delete('/lagunak/:id', (req, res) => {
    const { id } = req.params;
    const query = 'DELETE FROM lagunak WHERE id = ?';
    db.query(query, [id], (err, results) => {
        if (err) throw err;
        res.send(results);
    });
});


// Zerbitzaria hasieratu
const PORT = 3300;
app.listen(PORT, () => {
    console.log(`Zerbitzaria http://localhost:${PORT} -n martxan dago`);
});




