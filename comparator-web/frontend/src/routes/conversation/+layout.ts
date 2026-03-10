import { getAllConversations } from '$lib/api';
import type { LayoutLoad } from './$types';

export const load: LayoutLoad = async ({ depends }) => {
	depends('app:conversations');
	const conversations = await getAllConversations();
	return { conversations };
};
