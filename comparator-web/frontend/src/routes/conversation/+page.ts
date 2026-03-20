import {getAllConversations, getWellKnownIds} from '$lib/api';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ depends }) => {
	depends('app:conversations');
	const [ conversations, wellKnown ] = await Promise.all([
		getAllConversations(),
		getWellKnownIds(),
	])
	return { conversations, wellKnown };
};
