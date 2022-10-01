package io.github.tscholze.kotlog.terminal

import com.varabyte.kotter.foundation.anim.TextAnim
import com.varabyte.kotter.foundation.input.Completions
import com.varabyte.kotter.foundation.input.input
import com.varabyte.kotter.foundation.input.onInputEntered
import com.varabyte.kotter.foundation.input.runUntilInputEntered
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.*
import java.time.Duration

class Terminal {
    companion object {

        // MARK: - Private properties -

     //   val SPINNER_TEMPATE = TextAnim.Template(listOf("\\", "|", "/", "-"), Duration.ofMillis(250))


        // MARK: - Internal helper -

        fun generateConfiguration() {
          printGreeting()
        }

        // MARK: - Private helper -

       private fun printGreeting() {
            session {
                var commandIndex by liveVarOf(0)

                section {
                    p {
                        bold { textLine("Welcome to Kotlog 🤗") }
                        textLine("I will help you to streamline your workflows")
                    }
                    p {
                        text("Please choose ")
                        underline { text("a number") }
                        text(" to run the command:")
                    }

                    p {
                        textLine("""
                            | # | Command          |
                            |---|------------------|
                            | 0 | Create blog post |
                            |---|------------------|
                            | 1 | Create YT post   |
                            |---|------------------|
                            | 2 | Publish          |
                            |---|------------------|
                        """.trimIndent())
                    }


                    text("Which command should be executed in "); cyan { text("Kotlog") }; textLine("? (#)")
                    text("> "); input(Completions("0", "1", "2"))
                }.runUntilInputEntered {
                    onInputEntered {
                        commandIndex = processCommandIndexInput(input)

                        if(commandIndex == -1) {
                            rejectInput()
                        }

                        if(commandIndex == 0) {

                        } else if (commandIndex == 1){

                        } else if (commandIndex == 2) {

                        }
                    }
                }
            }
        }

        private fun processCommandIndexInput(input: String): Int {
            try {
                // Check if it is an int.
                // If not
                val index = input.toInt()

                // Check if it is in range.
                if(index < 0 || index > 2) {
                    return  -1
                }

                // If yes, return it.
                return index
            }
            catch (error: NumberFormatException) {
                return -1
            }

        }
    }
}