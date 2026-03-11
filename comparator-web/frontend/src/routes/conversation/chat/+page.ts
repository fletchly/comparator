import { getChatConversation } from '$lib/api';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ depends }) => {
	depends('app:conversations');
	const conversation = await getChatConversation();
	return { messages: conversation.messages };
};
