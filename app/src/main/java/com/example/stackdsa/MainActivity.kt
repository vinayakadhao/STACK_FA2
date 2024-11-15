package com.example.stackdsa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.graphics.Color
import java.util.*

class MainActivity : AppCompatActivity() {

    private val inputHistory = Stack<String>() // Stack to manage input history

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val checkButton = findViewById<Button>(R.id.checkButton)
        val clearButton = findViewById<Button>(R.id.clearButton)
        val undoButton = findViewById<Button>(R.id.undoButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        // Real-time validation
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                highlightUnbalancedParentheses(s.toString(), resultTextView)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Check balance button
        checkButton.setOnClickListener {
            val input = inputEditText.text.toString()
            inputHistory.push(input) // Push the current input to history
            val isBalanced = checkBalancedParentheses(input)

            resultTextView.text = if (isBalanced) {
                "The expression is balanced!"
            } else {
                "The expression is not balanced!"
            }

            resultTextView.visibility = View.VISIBLE
        }

        // Clear button
        clearButton.setOnClickListener {
            inputEditText.text.clear()
            resultTextView.text = ""
            resultTextView.visibility = View.GONE
        }

        // Undo button
        undoButton.setOnClickListener {
            if (inputHistory.isNotEmpty()) {
                inputHistory.pop() // Remove the current input from history
                if (inputHistory.isNotEmpty()) {
                    val lastInput = inputHistory.peek() // Get last input
                    inputEditText.setText(lastInput)
                    inputEditText.setSelection(lastInput.length) // Set cursor at the end
                } else {
                    inputEditText.text.clear()
                }
            }
        }
    }

    private fun checkBalancedParentheses(input: String): Boolean {
        val stack = Stack<Char>()
        val pairs = mapOf(')' to '(', '}' to '{', ']' to '[')

        for (char in input) {
            if (char in pairs.values) {
                stack.push(char)
            } else if (char in pairs.keys) {
                if (stack.isEmpty() || stack.pop() != pairs[char]) {
                    return false
                }
            }
        }

        return stack.isEmpty()
    }

    private fun highlightUnbalancedParentheses(input: String, resultTextView: TextView) {
        val stack = Stack<Int>()
        val pairs = mapOf(')' to '(', '}' to '{', ']' to '[')
        val spannable = SpannableStringBuilder(input)

        for (i in input.indices) {
            val char = input[i]
            if (char in pairs.values) {
                stack.push(i)
            } else if (char in pairs.keys) {
                if (stack.isEmpty() || input[stack.pop()] != pairs[char]) {
                    spannable.setSpan(ForegroundColorSpan(Color.RED), i, i + 1, 0)
                }
            }
        }

        resultTextView.text = spannable
        resultTextView.visibility = View.VISIBLE
    }
}
