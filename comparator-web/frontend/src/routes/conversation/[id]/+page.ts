import { getConversation, getWellKnownIds } from '$lib/api';
import { redirect, error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ params, depends }) => {
	depends('app:conversations');
	const { id } = params;

	const { console: consoleId, chat: chatId } = await getWellKnownIds();

	if (id === consoleId) redirect(307, '/conversation/console');
	if (id === chatId) redirect(307, '/conversation/chat');

	try {
		const messages = await getConversation(id);
		return { id, messages };
	} catch {
		error(404, 'Conversation not found');
	}
};
