import { getConsoleConversation } from '$lib/api';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ depends }) => {
	depends('app:conversations');
	const messages = await getConsoleConversation();
	return { messages };
};
