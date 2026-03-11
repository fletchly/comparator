import { serve } from 'bun';

interface MockConversation {
	displayName: string;
	messages: object[];
}

const mockConversations: Record<string, MockConversation> = {
	'00000000-0000-0000-0000-000000000001': {
		displayName: 'Console',
		messages: [
			{ type: 'user', content: 'Hello!', name: 'Console' },
			{ type: 'assistant', content: 'Hi! How can I help?', toolCalls: null }
		]
	},
	'00000000-0000-0000-0000-000000000002': {
		displayName: 'Chat',
		messages: []
	},
	'550e8400-e29b-41d4-a716-446655440000': {
		displayName: 'Steve',
		messages: [
			{ type: 'user', content: "What's a good sword enchantment?", name: 'Steve' },
			{
				type: 'assistant',
				content: 'Let me search',
				toolCalls: [
					{ name: 'web_search', arguments: { query: 'best sword enchantment' } },
					{ name: 'game_version', arguments: {} }
				]
			},
			{ type: 'tool', content: '{"Sharpness V"}', name: 'web_search' },
			{ type: 'tool', content: '{"version": "1.21.4"}', name: 'game_version' },
			{
				type: 'assistant',
				content: 'The best sword enchantment as of 1.21.4 is Sharpness V',
				toolCalls: null
			}
		]
	}
};

const WELL_KNOWN = {
	console: '00000000-0000-0000-0000-000000000001',
	chat: '00000000-0000-0000-0000-000000000002'
};

const mockTools = [
	{
		name: 'web_search',
		description: 'Search the web for information.',
		parameters: [
			{
				name: 'query',
				type: 'string',
				description: 'Search query',
				required: true,
				enum: null,
				'element-type': null
			}
		]
	},
	{
		name: 'game_version',
		description: 'Get the current Minecraft game version.',
		parameters: []
	},
	{
		name: 'current_date',
		description: 'Get the current date.',
		parameters: []
	},
	{
		name: 'player_info',
		description: 'Get info about the current player.',
		parameters: []
	}
];

function sseResponse(): Response {
	let interval: Timer;
	const stream = new ReadableStream({
		start(controller) {
			interval = setInterval(() => {
				controller.enqueue(new TextEncoder().encode(': heartbeat\n\n'));
			}, 5000);
		},
		cancel() {
			clearInterval(interval);
		}
	});
	return new Response(stream, {
		headers: {
			'Content-Type': 'text/event-stream',
			'Cache-Control': 'no-cache',
			Connection: 'keep-alive',
			'Access-Control-Allow-Origin': '*'
		}
	});
}

serve({
	port: 3001,
	fetch(req) {
		const url = new URL(req.url);
		const path = url.pathname;
		const method = req.method;

		const headers = {
			'Content-Type': 'application/json',
			'Access-Control-Allow-Origin': '*'
		};

		const json = (data: unknown, status = 200) =>
			new Response(JSON.stringify(data), { status, headers });

		const noContent = () => new Response(null, { status: 204, headers });

		if (path === '/api/version' && method === 'GET') return json({ version: '0.1.0-dev' });

		// SSE
		if (path === '/api/events') return sseResponse();

		// Tools
		if (path === '/api/tool' && method === 'GET') return json(mockTools);
		const toolMatch = path.match(/^\/api\/tool\/(.+)$/);
		if (toolMatch && method === 'GET') {
			const tool = mockTools.find((t) => t.name === toolMatch[1]);
			return tool ? json(tool) : new Response('Not Found', { status: 404 });
		}

		// Well-known
		if (path === '/api/conversation/well-known' && method === 'GET') return json(WELL_KNOWN);

		// Console
		if (path === '/api/conversation/console') {
			if (method === 'GET') return json(mockConversations[WELL_KNOWN.console]);
			if (method === 'DELETE') {
				mockConversations[WELL_KNOWN.console].messages = [];
				return noContent();
			}
		}

		// Chat
		if (path === '/api/conversation/chat') {
			if (method === 'GET') return json(mockConversations[WELL_KNOWN.chat]);
			if (method === 'DELETE') {
				mockConversations[WELL_KNOWN.chat].messages = [];
				return noContent();
			}
		}

		// Player conversations
		if (path === '/api/conversation/player' && method === 'GET') {
			const players = Object.fromEntries(
				Object.entries(mockConversations).filter(
					([id]) => id !== WELL_KNOWN.console && id !== WELL_KNOWN.chat
				)
			);
			return json(players);
		}

		// All conversations
		if (path === '/api/conversation' && method === 'GET') return json(mockConversations);
		if (path === '/api/conversation' && method === 'DELETE') {
			Object.values(mockConversations).forEach((c) => (c.messages = []));
			return noContent();
		}

		// Specific conversation by ID
		const convMatch = path.match(/^\/api\/conversation\/([^/]+)$/);
		if (convMatch) {
			const id = convMatch[1];
			if (method === 'GET') {
				const conv = mockConversations[id as keyof typeof mockConversations];
				return conv ? json(conv) : new Response('Not Found', { status: 404 });
			}
			if (method === 'DELETE') {
				if (mockConversations[id as keyof typeof mockConversations]) {
					mockConversations[id as keyof typeof mockConversations].messages = [];
					return noContent();
				}
				return new Response('Not Found', { status: 404 });
			}
		}

		return new Response('Not Found', { status: 404 });
	}
});

console.log('Mock API running on http://localhost:3001');
