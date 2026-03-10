import { getAllConversations } from '$lib/api';

export const load = async () => {
	const conversations = await getAllConversations();
	return { conversations };
};
