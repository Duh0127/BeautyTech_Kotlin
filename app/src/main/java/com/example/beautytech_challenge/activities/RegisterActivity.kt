package com.example.beautytech_challenge.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.beautytech_challenge.R

class RegisterActivity : Activity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.register_layout)

        val edtNome = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtCpf = findViewById<EditText>(R.id.edtCpf)
        val edtEndereco = findViewById<EditText>(R.id.edtEndereco)
        val edtDataNasc = findViewById<EditText>(R.id.edtDtNasc)
        val edtEstadoCivil = findViewById<EditText>(R.id.edtEstadoCivil)
        val edtCorPele = findViewById<EditText>(R.id.edtCorPele)
        val edtTipoCabeo = findViewById<EditText>(R.id.edtCabelo)
        val edtGen = findViewById<EditText>(R.id.edtGenero)

        val btnCadastro = findViewById<Button>(R.id.btnRegister)
        btnCadastro.setOnClickListener {
            val jsonBody = """
            {
                "nome": "${edtNome.text}",
                "email": "${edtEmail.text}",
                "cpf": "${edtCpf.text}",
                "endereco": "${edtEndereco.text}",
                "dataNasc": "${edtDataNasc.text}",
                "estadoCivil": "${edtEstadoCivil.text}",
                "corPele": "${edtCorPele.text}",
                "tipoCabelo": "${edtTipoCabeo.text}",
                "genero": "${edtGen.text}"
            }
            """.trimIndent()

            Toast.makeText(
                this@RegisterActivity,
                "JSON -> $jsonBody",
                Toast.LENGTH_LONG).show()

            // Limpa os campos de entrada
            edtNome.setText("")
            edtNome.setText("")
            edtEmail.setText("")
            edtCpf.setText("")
            edtEndereco.setText("")
            edtDataNasc.setText("")
            edtEstadoCivil.setText("")
            edtCorPele.setText("")
            edtTipoCabeo.setText("")
            edtGen.setText("")
        }



        val backButton = findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            // Crie um Intent para iniciar a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}