import { getConsoleConversation } from '$lib/api';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ depends }) => {
	depends('app:conversations');
	const conversation = await getConsoleConversation();
	return { messages: conversation.messages };
};
