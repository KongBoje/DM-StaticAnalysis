package vssl

import states.Range
import states.State

interface Expression

interface IntegerExpression : Expression

interface BooleanExpression : Expression

class Constant(val value: Int) : IntegerExpression {
    override fun toString() = "$value"
    }

class Variable(val name: String) : IntegerExpression {
    override fun toString() = "$name"
    }

class PlusExpression(
        val left: IntegerExpression,
        val right: IntegerExpression
        ) : IntegerExpression {
    override fun toString() = "$left + $right"
    }

class LessThanExpression(
        val left: IntegerExpression,
        val right: IntegerExpression
        ) : BooleanExpression {
    override fun toString() = "$left < $right"
    }

class GreaterThanAndEqualsExpression(
    val left: IntegerExpression,
    val right: IntegerExpression
) : BooleanExpression {
    override fun toString() = "$left >= $right"
}

interface Statement {
    fun analyse(precondition: State): State
    }

class DefinitionStatement(val name: String) : Statement {
    override fun analyse(precondition: State): State {
        precondition[name] = Range(0, 0, true)
        return precondition
        }

    override fun toString() = "DEF $name : INTEGER\n"
    }

class AssignmentStatement(
        val name: String,
        val value: IntegerExpression
        ) : Statement {
    override fun analyse(precondition: State): State {
        	precondition[name] = Range(0, Integer.MAX_VALUE, false)
        	return precondition
        }

    override fun toString() = "LET $name = $value\n"
    }

open class Block(vararg val statements: Statement) : Statement {
    override fun analyse(precondition: State): State {
        	var condition = precondition
        	for (statement in statements) {
            	condition = statement.analyse(condition)
        	}
        	return condition
        }

    override fun toString() =
        statements.joinToString(separator = "\n", prefix = "{\n", postfix = "}\n")
    }

class IfStatement(
        val predicate: BooleanExpression,
        val thenBlock: Block
        ) : Statement {
    override fun analyse(precondition: State): State {
        var condition = precondition
        for (statement in thenBlock.statements) {
            condition = statement.analyse(condition)
            }
        return condition
        }

    override fun toString() = """
        |IF $predicate THEN
        |$thenBlock
        """.trimMargin()
    }

class IfElseStatement(
        val predicate: BooleanExpression,
        val thenBlock: Block,
        val elseBlock: Block
        ) : Statement {
    override fun analyse(precondition: State): State {
        TODO("not implemented")
        }
    }

/**
 * Extends Block, by overriding toString with out the { }'s
 */
class Program (vararg statements: Statement) : Block(* statements) {

    override fun toString() =
        statements.joinToString(separator = "\n")

    }