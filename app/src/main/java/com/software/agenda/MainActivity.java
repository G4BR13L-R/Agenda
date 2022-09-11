package com.software.agenda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.software.bancoDados.ContatoDB;
import com.software.bancoDados.DBHelper;
import com.software.entidades.Contato;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Contato> todosDadosContatos;
    ListView viewContatos;

    DBHelper dbHelper;
    ContatoDB contatoDB;
    ArrayAdapter adapter;

    EditText editTextNome;
    EditText editTextTelefone;
    Button buttonSalvar;
    Button buttonCancelar;

    Contato contato;
    Boolean verificarEdicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(MainActivity.this);
        contatoDB = new ContatoDB(dbHelper);

        verificarEdicao = false;

        editTextNome = findViewById(R.id.campoNome);
        editTextTelefone = findViewById(R.id.campoTelefone);
        buttonSalvar = findViewById(R.id.botaoSalvar);
        buttonCancelar = findViewById(R.id.botaoCancelar);
        viewContatos = findViewById(R.id.listagemContatos);

        todosDadosContatos = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.support.constraint.R.layout.support_simple_spinner_dropdown_item, todosDadosContatos);

        viewContatos.setAdapter(adapter);
        contatoDB.listar(todosDadosContatos);
        acao();
    }

    private void acao() {
        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarEdicao) {
                    verificarEdicao = false;
                    contato = new Contato();
                    editTextNome.setText("");
                    editTextTelefone.setText("");
                }
            }
        });

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextNome.getText().toString().isEmpty() || editTextTelefone.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Contato Inválido!", Toast.LENGTH_SHORT).show();
                } else {
                    if (verificarEdicao == false) {
                        contato = new Contato();
                    }

                    contato.setNome(editTextNome.getText().toString());
                    contato.setTelefone(editTextTelefone.getText().toString());

                    if (verificarEdicao) {
                        contatoDB.atualizar(contato);
                    } else {
                        contatoDB.inserir(contato);
                    }

                    contatoDB.listar(todosDadosContatos);
                    adapter.notifyDataSetChanged();

                    verificarEdicao = false;
                    contato = new Contato();
                    editTextNome.setText("");
                    editTextTelefone.setText("");

                    Toast.makeText(MainActivity.this, "Salvo!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewContatos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(view.getContext())
                        .setMessage("Selecione uma Opção:")
                        .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                verificarEdicao = true;

                                contato = new Contato();
                                contato.setId(todosDadosContatos.get(i).getId());

                                editTextNome.setText(todosDadosContatos.get(i).getNome());
                                editTextTelefone.setText(todosDadosContatos.get(i).getTelefone());
                            }
                        })
                        .setNegativeButton("Remover", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                new AlertDialog.Builder(view.getContext())
                                        .setMessage("Remover o contato?")
                                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int k) {
                                                contatoDB.remover(todosDadosContatos.get(i).getId());
                                                contatoDB.listar(todosDadosContatos);
                                                adapter.notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton("Cancelar", null)
                                        .create().show();
                            }
                        })
                        .create().show();

                return false;
            }
        });
    }

}