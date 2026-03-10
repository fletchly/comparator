export type ArgumentValue =
	| string
	| number
	| boolean
	| null
	| ArgumentValue[]
	| { [key: string]: ArgumentValue };

export interface ToolCall {
	name: string;
	arguments: Record<string, ArgumentValue>;
}

export type Message =
	| { type: 'user'; content: string; name: string }
	| { type: 'assistant'; content: string; toolCalls: ToolCall[] | null }
	| { type: 'tool'; content: string; name: string };

export interface Parameter {
	name: string;
	type: 'string' | 'integer' | 'number' | 'boolean' | 'array' | 'object';
	description: string;
	required: boolean;
	enum: string[] | null;
	'element-type': string | null;
}

export interface Tool {
	name: string;
	description: string;
	parameters: Parameter[];
}
