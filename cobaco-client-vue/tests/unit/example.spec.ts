import { shallowMount } from "@vue/test-utils";
import Home from "@/views/Home.vue";

describe("Home.vue", () => {
  it("renders Home", () => {
    const msg = "ホーム";
    const wrapper = shallowMount(Home);
    expect(wrapper.text()).toMatch(msg);
  });
});
